package common.encryption;

public class TreeParityMachine {
    private int[] inputVectorData;
    private int[] weights, hiddenLayer;
    public int K, N, L;
    private int output;

    public TreeParityMachine(int n, int k, int l) {
        this.K = k;
        this.N = n;
        this.L = l;
        weights = new int[k * n];
        hiddenLayer = new int[k];
        randomize();
    }

    public void randomize(){
        for(int i=0;i<K*N;i++){
            weights[i]=(int)(Math.random()*L);
        }
    }

    public int computeOutput(InputVector inputVector) {
        inputVectorData = inputVector.getData();
        output=1;
        for (int i = 0; i < K; i++) {
            int sum = 0;
            for (int j = 0; j < N; j++) {
                sum += weights[i * N + j] * inputVectorData[i * N + j];
            }
            hiddenLayer[i] = sigma(sum);
            output *= sigma(sum);
        }

        return output;
    }
    public void updateWeight(LearningRule learningRule){
        switch (learningRule) {
            case HEBBIAN:
                applyHebbianLearningRule();
                break;
            case ANTI_HEBBIAN:
                applyAntiHebbianLearningRule();
                break;
            case RANDOM_WALK:
                applyRandomWalkLearningRule();
                break;
        }
    }

    private void applyHebbianLearningRule() {
        for (int i = 0;i < K; i++){
            for (int j = 0;j < N; j++){
                int newWeight= weights[i*N + j];
                newWeight += inputVectorData[i*N + j] * hiddenLayer[i] * theta(output, hiddenLayer[i]);
                if(newWeight > L) newWeight=L;
                if(newWeight < -L) newWeight=-L;
                weights[i*N + j]=newWeight;
            }
        }
    }

    private void applyAntiHebbianLearningRule() {
        for (int i = 0;i < K; i++){
            for (int j = 0; j < N; j++){
                int newWeight= weights[i*N + j];
                newWeight -= inputVectorData[i*N + j] * hiddenLayer[i] * theta(output, hiddenLayer[i]);
                if(newWeight > L) newWeight=L;
                if(newWeight < -L) newWeight=-L;
                weights[i*N + j]=newWeight;
            }
        }
    }

    private void applyRandomWalkLearningRule() {
        for(int i=0;i<K;i++){
            for(int j=0;j<N;j++){
                int newWeight= weights[i*N + j];
                newWeight += inputVectorData[i*N + j] * theta(output, hiddenLayer[i]);
                if(newWeight > L) newWeight=L;
                if(newWeight < -L) newWeight=-L;
                weights[i*N + j]=newWeight;
            }
        }
    }

    public static double meanSquaredError(int[] a, int[] b){
        double s=0;

        for(int i=0; i < a.length; i++){
            double err = Math.abs(a[i]-b[i]);
            s += err*err;
        }
        s /= a.length;

        return s;
    }

    public static double synchronizationStatus(int[] a, int[] b){
        double s=0;

        for(int i=0; i < a.length; i++){
            if (a[i] == b[i])
                s += 1;
        }
        s /= a.length;

        return s;
    }

    public static int sigma(double r) {
        return (r > 0) ? 1 : -1;
    }

    public static int theta(int a, int b){
        return (a == b) ? 1 : 0;
    }

    public int[] getWeights(){
        return weights;
    }

    public void display(){
        for(int i=0;i<K;i++)
            for(int j=0;j<N;j++){
                System.out.print(" "+ weights[i*N+j]);
            }
        System.out.println("");
    }

    public byte[] generateKey(){
        byte[] key = new byte[K*N];
        int absMax = absMaxWeight();

        for (int i = 0; i < K*N; i++) {
            key[i] = (byte) ((255 * weights[i])/absMax);
        }

        return key;
    }

    private int absMaxWeight() {
        int max = weights[0];

        for (int i = 0; i < K*N; i++) {
            if (weights[i] > max) {
                max = Math.abs(weights[i]);
            }
        }

        return max;
    }
}
