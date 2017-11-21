package Model.Traclus;

import java.awt.*;
import java.util.ArrayList;
import java.util.Queue;


/**
 * Created by wilhelmus on 19/11/17.
 */
public class Traclus {
    private static final int UNCLASSIFIED  = -1;
    private static final int NOISE = -2;
    private static final int REMOVED = -3;

    public Traclus(){

    }

    //BASIC FUNCTIONS
    public double d_euc(Point u, Point v){
        double squaredTotal = 0.0;
        squaredTotal+=Math.pow((u.getX()+v.getX()),2);
        squaredTotal+=Math.pow((u.getY()+v.getY()),2);
        return Math.sqrt(squaredTotal);
    }

    public double d2(Point u, Point v){
        double squaredTotal = 0.0;
        squaredTotal+=Math.pow((u.getX()+v.getX()),2);
        squaredTotal+=Math.pow((u.getY()+v.getY()),2);
        return squaredTotal;
    }

    public Point projection_point(Point u, Point v, Point p){
        Point proj = new Point();

        double l = d2(u,v);

        if (l < 1E-12)
            return u;

        double t = 0;
        t += ((v.getX()-u.getX())*(p.getX()-u.getX()));
        t += ((v.getY()-u.getY())*(p.getY()-u.getY()));
        t/=l;

        proj.setLocation(u.getX()+t*(v.getX()-u.getX()),u.getY()+t*(v.getY()-u.getY()));

        return proj;

    }

    //DISTANCE FUNCTIONS
    public double perpen_dist(Point si,Point ei,Point sj,Point ej)
    {
        Point ps = projection_point(si,ei,sj);
        Point pe = projection_point(si,ei,ej);

        double dl1 = d_euc(sj,ps);
        double dl2 = d_euc(ej,pe);

        if ((dl1+dl2) < 0.0001)
            return 0;

        return (dl1*dl1 + dl2*dl2)/(dl1 + dl2);
    }

    public double angle_dist(Point si,Point ei,Point sj,Point ej)
    {
        double alpha1 = Math.atan2(ei.getY() - si.getY(),ei.getX() - si.getX());
        double alpha2 = Math.atan2(ej.getY() - sj.getY(),ej.getX() - sj.getX());

        double l = d_euc(sj,ej);

        return l * (Math.sin(alpha2-alpha1));
    }

    double par_dist(Point si,Point ei,Point sj,Point ej)
    {
        Point ps = projection_point(si,ei,sj);
        Point pe = projection_point(si,ei,ej);

        double l1 = Math.min(d_euc(ps,si),d_euc(ps,ei));
        double l2 = Math.min(d_euc(pe,si),d_euc(pe,ei));

        return Math.min(l1,l2);
    }

    double total_dist(Point si,Point ei,Point sj,Point ej, double w_perpendicular, double w_parallel, double w_angle)
    {
        if(w_angle==0)w_angle=0.33;
        if(w_perpendicular==0)w_perpendicular=0.33;
        if(w_parallel==0)w_parallel=0.33;
        double td = w_perpendicular * perpen_dist(si,ei,sj,ej)
                + w_parallel * par_dist(si,ei,sj,ej)
                + w_angle * angle_dist(si,ei,sj,ej);
        return td;
    }


    public double MDL_PAR(ArrayList<Point> st, Point en)
    {
        double d1 = d_euc(st.get(0), en);
        double PER_DIST=0, ANG_DIST=0;
        if(st.get(st.size()-1)!=en)
            st.add(en);
        if(st.size()>1){
            int i = 1;
            Point it = st.get(0);
            Point it2;
            it2 = st.get(i);
            while (true)
            {
                double d2 = d_euc(it,it2);
                if (d1 >= d2)
                {
                    PER_DIST += perpen_dist(st.get(0),en,it,it2);
                    ANG_DIST += angle_dist(st.get(0),en,it,it2);
                }else{
                    PER_DIST += perpen_dist(it,it2,st.get(0),en);
                    ANG_DIST += angle_dist(it,it2,st.get(0),en);
                }
                if (it2 == en)
                    break;
                i++;
                it = it2;
                it2 = st.get(i);
            }
        }

        // information calculation
        double LH = Math.log(d1)/Math.log(2);
        double LDH = Math.log(PER_DIST)/Math.log(2) + Math.log(ANG_DIST)/Math.log(2);

        return LH + LDH;
    }

    ArrayList<Point> traclus_partition (ArrayList<Point> A)
    {
        ArrayList<Point> CP = new ArrayList<>();
        CP.add(A.get(0));
        if(A.size()>1){
            Point it = A.get(0),it2 = A.get(0),it2_old = A.get(0);
            int i = 1;
            it2 = A.get(i);
            ArrayList<Point> temp = new ArrayList<>(CP);
            while (it2 != A.get(A.size()-1))
            {
                double cost = MDL_PAR(temp, it2);
                double cost2 = Math.log(d_euc(it,it2))/Math.log(2);
                i++;
                if (cost > cost2 && !((cost) < 0.0001)) // right side: skip over equal points
                {
                    CP.add(it2_old);
                    temp.add(it2);
                    while(!temp.get(0).equals(it2_old))
                        temp.remove(0);
                    it2_old = it2;
                    it2 = A.get(i);
                }else{
                    it2_old = it2;
                    it2 = A.get(i);
                }
            }
            CP.add(A.get(A.size()-1));
        }
        return CP;
    }

    ArrayList<Point> compute_NeIndizes(ArrayList<Point> L,int idx, double eps)
    {
        ArrayList<Point> ret = new ArrayList<>();
        for (int i=0; i < L.size(); i++)
            if (idx != i)
                if (d_euc(L.get(i),L.get(idx)) <= eps)
                {
                    ret.add(L.get(i));
                }
        return ret;
    }


}
