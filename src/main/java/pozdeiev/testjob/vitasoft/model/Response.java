package pozdeiev.testjob.vitasoft.model;

public interface Response<T> {

    T getResult();

    String getError();
}
