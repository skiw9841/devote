package loopchain.sdk.service.crypto;

import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;

public class PKIUtils {

    public static final String PROVIDER = "SC";
    public static final String ALGORITHM_KEY = "EC";
    public static final String ALGORITHM_PARAM = "secp256k1";
    public static final int ALGORITHM_KEY_LENGTH = 32;
    public static final String ALGORITHM_HASH = "SHA3-256";
    public static final String ETHER_ADD_ALGO = "Keccak-256";


    private static final ECParameterSpec EC_SPEC = ECNamedCurveTable.getParameterSpec(ALGORITHM_PARAM);

    public static byte[] hash(byte[] message, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest md = MessageDigest.getInstance(algorithm, PROVIDER);
        return md.digest(message);
    }

    public static String b64Encode(byte[] message) {
        return Base64.toBase64String(message);
    }

    public static byte[] b64Decode(String b64Data) {
        return Base64.decode(b64Data);
    }

    public static String hexEncode(byte[] message) {
        return Hex.toHexString(message);
    }

    public static byte[] hexDecode(String hexData) {
        return Hex.decode(hexData);
    }

    public static KeyPair generateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_KEY, PROVIDER);
        kpg.initialize(EC_SPEC, new SecureRandom());
        return kpg.generateKeyPair();
    }

    public static boolean checkPrivateKey(byte[] input) {
        BigInteger n = EC_SPEC.getN();

        BigInteger d = new BigInteger(1, input);
        if (d.compareTo(BigInteger.valueOf(2)) < 0 || (d.compareTo(n) >= 0))
            return false;
        else
            return true;
    }

    public static String makeAddressFromPrivateKey(String hexPrivateKey) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] privateKey = Hex.decode(hexPrivateKey);
        byte[] publicKey = getPublicKeyFromPrivateKey(privateKey, false);
        return makeAddress(publicKey);
    }

    public static String makeAddressFromPrivateKey(byte[] privateKey) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] publicKey = getPublicKeyFromPrivateKey(privateKey, false);
        return makeAddress(publicKey);
    }

    public static String makeAddress(byte[] publicKey) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM_HASH, PROVIDER);
        byte[] hash = null;
        if (publicKey.length > 64) {
            md.update(publicKey, 1, 64);
            hash = md.digest();
        } else
            hash = md.digest(publicKey);

        return "hx" + Hex.toHexString(hash, hash.length - 20, 20);
    }

    public static String makeEtherAddress(byte[] publicKey) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest md = MessageDigest.getInstance(ETHER_ADD_ALGO, PROVIDER);
        byte[] hash = null;
        if (publicKey.length > 64) {
            md.update(publicKey, 1, 64);
            hash = md.digest();
        } else
            hash = md.digest(publicKey);

        return Hex.toHexString(hash, hash.length - 20, 20);
    }

    public static byte[] getPublicKeyFromPrivateKey(byte[] privBytes, boolean addPrefix) {
        ECPoint pointQ = EC_SPEC.getG().multiply(new BigInteger(1, privBytes));
        if (addPrefix)
            return pointQ.getEncoded(false);
        else
            return ecpoint2bytes(pointQ);
    }

    //UnsignedByte
    public static byte[] ecPrivateKey2Bytes(PrivateKey privKey) {
        if (privKey instanceof ECPrivateKey) {
            BigInteger d = ((ECPrivateKey) privKey).getD();
            return asUnsignedByteArray(ALGORITHM_KEY_LENGTH, d);
        } else {
            return null;
        }
    }

    public static BigInteger[] sign(byte[] message, byte[] privKey) {
        ECDomainParameters domain = new ECDomainParameters(EC_SPEC.getCurve(), EC_SPEC.getG(), EC_SPEC.getN());

        BigInteger d = new BigInteger(1, privKey);
        ECPrivateKeyParameters privateKeyParms = new ECPrivateKeyParameters(d, domain);

        ECDSASigner ecdsaSigner = new ECDSASigner();
        ecdsaSigner.init(true, privateKeyParms);

        return ecdsaSigner.generateSignature(message);
    }

    public static String sign(byte[] hashedTBS, String hexPrivKey) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] privateKey = Hex.decode(hexPrivKey);
        BigInteger[] sign = PKIUtils.sign(hashedTBS, privateKey);
        byte[] publicKey = PKIUtils.getPublicKeyFromPrivateKey(privateKey, true);

        byte recoveryId = PKIUtils.getRecoveryId(sign, hashedTBS, publicKey);
        System.out.println("KS recoveryID=" + recoveryId);
        byte[] signData = PKIUtils.getSignature(sign[0], sign[1], new byte[]{recoveryId});
        return PKIUtils.b64Encode(signData);
    }

    public static byte[] verify(byte[] hashedTBS, String b64Signature) {
        byte[] signature = PKIUtils.b64Decode(b64Signature);
        return PKIUtils.recoverPublicKey(signature, hashedTBS);
    }

    public static byte[] getSignature(BigInteger R, BigInteger S, byte[] recoveryId) {
        byte[] r = asUnsignedByteArray(ALGORITHM_KEY_LENGTH, R);
        byte[] s = asUnsignedByteArray(ALGORITHM_KEY_LENGTH, S);
        byte[] signature = new byte[r.length + s.length + 1];

        System.arraycopy(r, 0, signature, 0, r.length);
        System.arraycopy(s, 0, signature, r.length, s.length);
        System.arraycopy(recoveryId, 0, signature, r.length + s.length, 1);
        return signature;
    }

    public static byte[] recoverPublicKey(byte[] signature, byte[] message) {
        byte[] sigR = new byte[32];
        byte[] sigS = new byte[32];
        byte[] sigV = new byte[1];
        System.arraycopy(signature, 0, sigR, 0, 32);
        System.arraycopy(signature, 32, sigS, 0, 32);
        System.arraycopy(signature, 64, sigV, 0, 1);

        BigInteger pointN = EC_SPEC.getN();

        BigInteger pointX = new BigInteger(1, sigR);

        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(pointX, 1 + x9.getByteLength(EC_SPEC.getCurve()));
        compEnc[0] = (byte) ((sigV[0] & 1) == 1 ? 0x03 : 0x02);
        ECPoint pointR = EC_SPEC.getCurve().decodePoint(compEnc);
        if (!pointR.multiply(pointN).isInfinity()) {
            return new byte[0];
        }

        BigInteger pointE = new BigInteger(1, message);
        BigInteger pointEInv = BigInteger.ZERO.subtract(pointE).mod(pointN);
        BigInteger pointRInv = new BigInteger(1, sigR).modInverse(pointN);
        BigInteger srInv = pointRInv.multiply(new BigInteger(1, sigS)).mod(pointN);
        BigInteger pointEInvRInv = pointRInv.multiply(pointEInv).mod(pointN);
        ECPoint pointQ = ECAlgorithms.sumOfTwoMultiplies(EC_SPEC.getG(), pointEInvRInv, pointR, srInv);
        return pointQ.getEncoded(false);
    }

    public static byte getRecoveryId(BigInteger[] sign, byte[] message, byte[] publicKey) {
        BigInteger pointN = EC_SPEC.getN();

        for (int recoveryId = 0; recoveryId < 2; recoveryId++) {
            BigInteger pointX = sign[0];

            X9IntegerConverter x9 = new X9IntegerConverter();
            byte[] compEnc = x9.integerToBytes(pointX, 1 + x9.getByteLength(EC_SPEC.getCurve()));
            compEnc[0] = (byte) ((recoveryId & 1) == 1 ? 0x03 : 0x02);
            ECPoint pointR = EC_SPEC.getCurve().decodePoint(compEnc);
            if (!pointR.multiply(pointN).isInfinity()) {
                continue;
            }

            BigInteger pointE = new BigInteger(1, message);
            BigInteger pointEInv = BigInteger.ZERO.subtract(pointE).mod(pointN);
            BigInteger pointRInv = sign[0].modInverse(pointN);
            BigInteger srInv = pointRInv.multiply(sign[1]).mod(pointN);
            BigInteger pointEInvRInv = pointRInv.multiply(pointEInv).mod(pointN);
            ECPoint pointQ = ECAlgorithms.sumOfTwoMultiplies(EC_SPEC.getG(), pointEInvRInv, pointR, srInv);
            byte[] pointQBytes = pointQ.getEncoded(false);

            boolean matchedKeys = true;
            for (int j = 0; j < publicKey.length; j++) {
                if (pointQBytes[j] != publicKey[j]) {
                    matchedKeys = false;
                    break;
                }
            }
            if (!matchedKeys) {
                continue;
            }
            return (byte) (0xFF & recoveryId);
        }
        return (byte) 0xFF;
    }

    public static byte[] ecpoint2bytes(ECPoint p) {
        ECPoint q = p.normalize();
        byte[] x = q.getXCoord().getEncoded();
        byte[] y = q.getYCoord().getEncoded();

        byte[] out = new byte[x.length + y.length];
        System.arraycopy(x, 0, out, 0, x.length);
        System.arraycopy(y, 0, out, x.length, y.length);
        return out;
    }

    public static byte[] asUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        }

        int start = bytes[0] == 0 ? 1 : 0;
        int count = bytes.length - start;

        if (count > length) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }

        byte[] tmp = new byte[length];
        System.arraycopy(bytes, start, tmp, tmp.length - count, count);
        return tmp;
    }
}
