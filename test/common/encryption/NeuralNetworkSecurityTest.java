package common.encryption;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class NeuralNetworkSecurityTest {
    private Random random = new Random();
    private final int numberOfIterations = 10000;

    @Test
    public void encryptAndDecryptTest() throws Exception {
        int K = 4;
        int N = 8;
        int L = 4;

        TreeParityMachine treeParityMachine = new TreeParityMachine(K, N, L);
        byte[] key = treeParityMachine.generateKey();


        for (int i = 0; i < numberOfIterations; i++) {
            byte[] expected = new byte[random.nextInt(numberOfIterations) + numberOfIterations];
            random.nextBytes(expected);

            byte[] encryptedData = NeuralNetworkSecurity.encrypt(expected, key);
            byte[] decryptedData = NeuralNetworkSecurity.decrypt(encryptedData, key);

            assertArrayEquals(expected, decryptedData);
        }

    }
}