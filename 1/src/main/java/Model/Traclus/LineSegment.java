package Model.Traclus;

import java.awt.*;

/**
 * Created by wilhelmus on 19/11/17.
 */


public class LineSegment {
    private static final int UNCLASSIFIED  = -1;
    private static final int NOISE = -2;
    private static final int REMOVED = -3;

    public int cluster;
    public Point s;
    public Point e;

    public LineSegment(){
        s.setLocation(0.0,0.0);
        e.setLocation(0.0,0.0);
        cluster = UNCLASSIFIED;
    }

    public LineSegment(Point _s, Point _e){
        s = _s;
        e = _e;
        cluster = UNCLASSIFIED;
    }

}
