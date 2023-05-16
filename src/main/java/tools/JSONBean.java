package tools;

import java.util.List;

public class JSONBean {
    private int errorCode;
    private List<Object> data;
    private String message;

    public JSONBean(){}
    public JSONBean(int errorCode, List<Object> data, String message){
        this.errorCode = errorCode;
        this.data = data;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}