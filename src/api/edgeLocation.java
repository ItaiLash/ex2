package api;

import java.util.Collection;

public class edgeLocation implements edge_location{

    private directed_weighted_graph g;
    private geo_location point;

    public edgeLocation(directed_weighted_graph gra, geo_location p){
        this.g = gra;
        this.point = p;
    }

    @Override
    public edge_data getEdge() {
        Collection<node_data> nodes = g.getV();
        for(node_data n : nodes) {
            Collection<edge_data> edges = g.getE (n.getKey());
            for(edge_data e : edges){
                geo_location src = n.getLocation();
                geo_location dest = g.getNode(e.getDest()).getLocation();
                  if((point.x()-src.x())/(dest.x()-src.x()) == (point.y()-src.y())/(dest.y()-src.y()) &&
                        (point.y()-src.y())/(dest.y()-src.y()) == (point.z()-src.z())/(dest.z()-src.z())){
                            return e;
                }
            }
        }
        return null;
    }

    @Override
    public double getRatio() {
        if(this.getEdge()!=null){
            int src = this.getEdge().getSrc();
            int dest = this.getEdge().getDest();
            double ratio = this.g.getNode(src).getLocation().distance(point)/this.g.getNode(src).getLocation().distance(this.g.getNode(dest).getLocation());
            return ratio;
        }
        return -1;
    }
}
