package DataProcess.WindowProcess;

import Model.Instances.Instance;
import Model.Traclus.LineSegment;
import Model.Traclus.Traclus;
import Preprocess.JSONPathTraverse;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wilhelmus on 27/11/17.
 */
public class TrajectoryClustering implements WindowProcessInterface{
    private ArrayList<ArrayList<Instance>> allTrajectories;
    private ArrayList<Instance> allInstances;
    private static JSONPathTraverse pathTraverse;
    private Set<String> allID;
    private static double eps = 161.9;
    private static int minLines = 3;

    public TrajectoryClustering(){
        pathTraverse = new JSONPathTraverse();
        allTrajectories = new ArrayList<>();
        allInstances = new ArrayList<>();
        allID = new HashSet<>();
    }

    @Override
    public String processData() {
        Traclus T = new Traclus();
        ArrayList<LineSegment> segments = new ArrayList<>();
        for( ArrayList<Instance> it: allTrajectories)
        {
            if(it != null && it.size()>1) {
                String id = it.get(0).getId();
                ArrayList<Point2D> points = new ArrayList<>();
                for(Instance inst : it){
                    Point2D A = new Point2D.Double();
                    A.setLocation(pathTraverse.solve("entity[0].vehicle.position.longitude",inst.getJson()).getDoubleValue(),pathTraverse.solve("entity[0].vehicle.position.latitude",inst.getJson()).getDoubleValue());
                    points.add(A);
                }
                ArrayList<Point2D> newA = T.traclus_partition(points);
                if(newA.size()>1) {
                    for (int i = 0; i < newA.size() - 1; i++) {
                        LineSegment ls = new LineSegment(newA.get(i), newA.get(i + 1), id);
                        segments.add(ls);
                    }
                }
            }
        }
        if(segments.size()>1)
            return T.grouping(segments,eps,minLines).toString();
        else
            return segments.toString();
    }

    @Override
    public void preProcessData(Instance inst) {
        if(!allID.contains(inst.getId().toString())){
            ArrayList<Instance> A = new ArrayList<>();
            A.add(inst);
            allTrajectories.add(A);
        }else{
            for(int i=0;i<allTrajectories.size();i++){
                if(allTrajectories.get(i).get(0).getId().equals(inst.getId())){
                    allTrajectories.get(i).add(inst);
                }
            }
        }
        allID.add(inst.getId());
    }
}
