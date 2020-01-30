package loopchain.sdk.core.request;

import android.util.Log;
import com.google.gson.JsonObject;
import loopchain.sdk.core.Constants;
import loopchain.sdk.service.crypto.SendTransactionSigner;

public class SendTransactionData extends RequestData {

    private static final String FEE = "0x2386f26fc10000";
    private static final String NONCE = "8367273";

    public SendTransactionData(String id, String from, String to, String value, String hexPrivateKey) {
        this.method = Constants.METHOD_SENDTRANSACTION;
        this.id = id;

        String timestamp = getTimeStamp();

        SendTransactionSigner signer;
        signer = new SendTransactionSigner(FEE, from, timestamp, to, value, NONCE);

        String txHash = signer.getTxHash();
        String signature = signer.getSignature(txHash, hexPrivateKey);

        JsonObject params = new JsonObject();
        params.addProperty("from", from);
        params.addProperty("to", to);
        params.addProperty("value", value);
        params.addProperty("fee", FEE);
        params.addProperty("timestamp", timestamp);
        params.addProperty("nonce", NONCE);
        params.addProperty("tx_hash", txHash);
        params.addProperty("signature", signature);

        Log.d("SendTransactionData", "rqData=" + params.toString());

        this.params = params;
    }
}
