package fr.insa.jchat.common;

import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidRequestException;
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
            request.setParams(param[0], param[1]);
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

                LOGGER.debug("Body : {}", (Object)body);
                request.setBody(new String(body));
            }
        }
        catch(NumberFormatException e) {
            throw new InvalidParamValue("length param must be strictly greater than 0", e, "length");
        }

        return request;
    }

    public static Request createErrorResponse(InvalidRequestException e) {
        Request request = new Request();
        request.setMethod(Request.Method.ERROR);
        request.setParams("errorName", e.getErrorName());

        try {
            Field[] fields = e.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                request.setParams(field.getName(), field.get(e).toString());
            }
        }
        catch(IllegalAccessException ex) {
            LOGGER.error("An error occured while creating error response", ex);
        }

        return request;
    }

    public static void sendResponse(Request response, PrintStream out) {
        out.println(response.getMethod());

        for(String param : response.getParamNames())
            out.println(param + ":" + response.getParam(param));

        out.println();

        if(response.getBody() != null && response.getBody().length() > 0)
            out.println(response.getBody());
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
