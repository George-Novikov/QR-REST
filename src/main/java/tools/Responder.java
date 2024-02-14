package tools;

import constants.ServiceMessage;

import javax.ws.rs.core.Response;
import java.util.List;

public class Responder {
    public static Response sendOk(List<Object> data){
        return Response.ok(new DataJSON(0, data, ServiceMessage.OK_MESSAGE.get())).build();
    }

    public static Response sendOk(byte[] data){
        return Response.ok(data).build();
    }

    public static Response sendError(int statusCode, ServiceMessage message){
        return Response.status(500).entity(new DataJSON(statusCode, message.get())).build();
    }

    public static Response sendError(int statusCode, ServiceMessage message, Exception e){
        return Response.status(500).entity(
                new DataJSON(
                        statusCode, String.format("%s %s", message.get(), e.getMessage())
                )
        ).build();
    }
}
