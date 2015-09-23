public class QuizzDataDistanceWrapper {
    private QuizzData data;
    private float distance;

    public QuizzData getData() {
        return data;
    }

    public float getDistance() {
        return distance;
    }

    public QuizzDataDistanceWrapper(QuizzData data, float distance) {
        this.data = data;
        this.distance = distance;
    }
}