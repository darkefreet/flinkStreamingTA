package Model.Traclus;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.awt.geom.Point2D;

/**
 * Created by wilhelmus on 19/11/17.
 */


public class LineSegment {
    private static final int UNCLASSIFIED  = -1;
    private static final int NOISE = -2;
    private static final int REMOVED = -3;

    public int cluster;
    public String trajectory_index;
    public Point2D s;
    public Point2D e;

    public LineSegment(){
        s.setLocation(0.0,0.0);
        e.setLocation(0.0,0.0);
        cluster = UNCLASSIFIED;
    }

    public LineSegment(Point2D _s, Point2D _e,String _trajectory_index){
        s = _s;
        e = _e;
        trajectory_index = _trajectory_index;
        cluster = UNCLASSIFIED;
    }

    @Override
    public String toString(){
        ObjectMapper jsonParser = new ObjectMapper();
        JsonNode data = jsonParser.createObjectNode();
        ((ObjectNode)data).put("id",trajectory_index);
        ((ObjectNode)data).put("start_longitude",s.getX());
        ((ObjectNode)data).put("start_latitude",s.getY());
        ((ObjectNode)data).put("end_longitude",e.getX());
        ((ObjectNode)data).put("end_latitude",e.getY());
        ((ObjectNode)data).put("cluster",cluster);
        return data.toString();
    }

}
