package pozdeiev.testjob.vitasoft.model;

public class CommonResponse<T> implements Response<T> {

    private final T result;
    private final String error;

    public CommonResponse(T result, String error) {
        this.result = result;
        this.error = error;
    }

    public static <T> CommonResponse<T> success(T result) {
        return new CommonResponse<>(result, null);
    }

    public static <T> CommonResponse<T> error(String error) {
        return new CommonResponse<>(null, error);
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public String getError() {
        return error;
    }
}
