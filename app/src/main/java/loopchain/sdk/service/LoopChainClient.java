package loopchain.sdk.service;

import loopchain.sdk.core.RESTClient;
import loopchain.sdk.core.request.GetBalanceData;
import loopchain.sdk.core.request.SendTransactionData;
import loopchain.sdk.core.response.LCResponse;
import loopchain.sdk.service.crypto.PKIUtils;
import retrofit2.Call;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class LoopChainClient {

    private RESTClient restClient;
    private SecureRandom random;
    private BigInteger decimal = new BigInteger("1000000000000000000");

    public LoopChainClient(String host) throws Exception {
        restClient = new RESTClient(host);
        random = new SecureRandom();
    }

    public Call<LCResponse> sendTransaction(String from, String to, String value, String hexPrivateKey) throws IOException {
        String id = getRandomId();
        if (!value.startsWith("0x")) {
            value = icxToHexString(value);
        }

        SendTransactionData txData = new SendTransactionData(id, from, to, value, hexPrivateKey);
        Call<LCResponse> response = restClient.sendRequest(txData);
        return response;
    }

    public Call<LCResponse> getBalance(String address) throws IOException {
        String id = getRandomId();
        GetBalanceData qData = new GetBalanceData(id, address);
        Call<LCResponse> response = restClient.sendRequest(qData);
        return response;
    }

    public String printICX(String value) {
        if (value.startsWith("0x"))
            value = value.substring(2);
        else
            return value;

        BigInteger[] total = new BigInteger(value, 16).divideAndRemainder(decimal);

        String icx = total[0].toString();
        String wei = total[1].toString();
        while (wei.length() < 18)
            wei = "0" + wei;

        return icx + "." + wei;
    }

    public String icxToHexString(String value) {
        int start = value.indexOf(".");
        if (start < 1) {
            BigInteger icx = new BigInteger(value);
            icx = icx.multiply(decimal);
            return "0x" + icx.toString(16);
        } else {
            BigInteger icx = new BigInteger(value.substring(0, start));
            icx = icx.multiply(decimal);

            String tmp = value.substring(start + 1);
            while (tmp.length() < 18)
                tmp = tmp + "0";
            BigInteger dot = new BigInteger(tmp);

            icx = icx.add(dot);
            value = "0x" + icx.toString(16);
            return value;
        }
    }

    private String getRandomId() {
        return PKIUtils.hexEncode(random.generateSeed(3));
    }
}
