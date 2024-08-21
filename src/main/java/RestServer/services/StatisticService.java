package RestServer.services;

import RestServer.beans.statistics.StatisticBean;
import RestServer.beans.statistics.Statistics;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("statistics")
public class StatisticService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getStatistics(){
        return Response.ok(Statistics.getInstance()).build();

    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addStatistic(StatisticBean u){
        Statistics.getInstance().addStatistic(u);
        return Response.ok().build();
    }

    @Path("get")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getStatsList(){
        ArrayList<StatisticBean> l = Statistics.getInstance().getStatsList();
        return Response.ok(l).build();
    }

    @Path("get/{id}/{n}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getLastStats(@PathParam("id") int id, @PathParam("n") int n) {
        double averageLastN = Statistics.getInstance().getRecentStatistics(id, n);
        return Response.ok(averageLastN).build();
    }

    @Path("get/{t1}-{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageAirPollution(@PathParam("t1") long t1, @PathParam("t2") long t2){
        double average = Statistics.getInstance().avgStatistics(t1, t2);
        return Response.ok(average).build();
    }

}
