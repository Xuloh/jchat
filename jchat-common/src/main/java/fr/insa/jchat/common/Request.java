package fr.insa.jchat.common;

import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidRequestException;
import fr.insa.jchat.common.exception.MissingBodyException;
import fr.insa.jchat.common.exception.MissingParamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class Request {
    private static final Logger LOGGER = LogManager.getLogger(Request.class);

    private Method method;

    private Map<String, String> params;

    private String body;

    public static Request createRequestFromReader(BufferedReader in) throws IOException, InvalidParamValue, InvalidBodySizeException, InvalidMethodException {
        Request request = new Request();

        // read request method
        String line = in.readLine();
        LOGGER.debug("Method : {}", line);
        try {
            request.setMethod(Request.Method.valueOf(line));
        }
        catch(IllegalArgumentException e) {
            throw new InvalidMethodException(line);
        }

        // read request params
        line = in.readLine();
        while(line.length() > 0) {
            String[] param = line.split(":", 2);
            LOGGER.debug("Param : {}", (Object)param);
            request.setParam(param[0], param[1]);
            line = in.readLine();
        }

        // handle body if length param is present
        try {
            if(request.hasParam("length")) {
                // parse length value
                int length = Integer.parseInt(request.getParam("length"));
                if(length < 0)
                    throw new NumberFormatException("For input string : " + length);

                // read that many characters from input
                char[] body = new char[length];
                int nbRead = in.read(body, 0, length);

                // check that enough characters were read
                if(nbRead != length)
                    throw new InvalidBodySizeException(length, nbRead);

                String bodyStr = new String(body);
                LOGGER.debug("Body : {}", bodyStr);
                request.setBody(bodyStr);
            }
        }
        catch(NumberFormatException e) {
            throw new InvalidParamValue("length param must be strictly greater than 0", e, "length");
        }

        return request;
    }

    public static void requiredParams(Request request, String... params) throws MissingParamException {
        for(String param : params) {
            if(!request.params.containsKey(param))
                throw new MissingParamException(param);
            else if(request.params.get(param).length() == 0)
                throw new MissingParamException(param);
        }
    }

    public static void requireBody(Request request) throws MissingBodyException {
        if(request.body == null || request.body.length() == 0)
            throw new MissingBodyException();
    }

    public static Request createErrorResponse(InvalidRequestException e) {
        Request request = new Request();
        request.setMethod(Request.Method.ERROR);
        request.setParam("errorName", e.getErrorName());

        try {
            Field[] fields = e.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                request.setParam(field.getName(), field.get(e).toString());
            }
        }
        catch(IllegalAccessException ex) {
            LOGGER.error("An error occured while creating error response", ex);
        }

        return request;
    }

    public static void sendRequest(Request request, PrintStream out) {
        String requestStr = format(request);
        out.print(requestStr);
    }

    public static String format(Request request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod()).append('\n');

        for(String param : request.params.keySet()) {
            builder
                .append(param)
                .append(':')
                .append(request.params.get(param))
                .append('\n');
        }
        builder.append('\n');

        if(request.body != null && request.body.length() > 0)
            builder.append(request.body);

//        LOGGER.debug("Formatted request : {}", builder.toString());
        return builder.toString();
    }

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

    public void setParam(String name, String value) {
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
        ERROR,
        NEW_USER
    }
}
