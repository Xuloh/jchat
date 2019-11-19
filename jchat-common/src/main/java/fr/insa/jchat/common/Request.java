package fr.insa.jchat.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Request {
    private Method method;

    private Map<String, String> params;

    private String body;

    public Request() {
        this.params = new HashMap<>();
    }

    public boolean hasParam(String param) {
        return this.params.containsKey(param);
    }

    public Set<String> getParamNames() {
        return this.params.keySet();
    }

    public String getParam(String param) {
        if(!this.params.containsKey(param))
            throw new NoSuchElementException("Request doesn't have request param : " + param);
        return this.params.get(param);
    }

    public void setParams(String name, String value) {
        this.params.put(name, value);
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{" + "type=" + method + ", params=" + params + ", body='" + body + '\'' + '}';
    }

    public enum Method {
        REGISTER,
        LOGIN,
        GET,
        MESSAGE,
        USER_STATUS,
        DISCOVER,
        OK,
        ERROR
    }
}
