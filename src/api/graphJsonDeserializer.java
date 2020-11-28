package api;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class graphJsonDeserializer implements JsonDeserializer<directed_weighted_graph> {
    @Override
    public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        directed_weighted_graph graph = new DWGraph_DS();
        JsonObject nodesJsonObject = jsonObject.get("nodes").getAsJsonObject();

        for(Map.Entry<String,JsonElement> set : nodesJsonObject.entrySet()){
            JsonObject jsonObjectNode = set.getValue().getAsJsonObject();
            int key = jsonObjectNode.get("key").getAsInt();
            double weight = jsonObjectNode.get("weight").getAsDouble();
            String info = jsonObjectNode.get("info").getAsString();
            int tag = jsonObjectNode.get("tag").getAsInt();
            JsonObject locationJsonObject = jsonObjectNode.get("location").getAsJsonObject();
            double x = locationJsonObject.get("x").getAsDouble();
            double y = locationJsonObject.get("y").getAsDouble();
            double z = locationJsonObject.get("z").getAsDouble();
            geo_location location = new geoLocation(x,y,z);
            node_data n = new node(key,location,weight,info,tag,Double.MAX_VALUE,null);
            graph.addNode(n);
        }
        JsonObject edgesJsonObject = jsonObject.get("edges").getAsJsonObject();
        for(Map.Entry<String, JsonElement> set : edgesJsonObject.entrySet()){
            int counter = 0;
            for(Map.Entry<String, JsonElement> set2 : set.getValue().getAsJsonObject().entrySet()){
                JsonObject jsonObjectEdge = set2.getValue().getAsJsonObject();
                double weight = jsonObjectEdge.get("weight").getAsDouble();
                graph.connect(Integer.parseInt(set.getKey()),Integer.parseInt(set2.getKey()),weight);
                String info = jsonObjectEdge.get("info").getAsString();
                int tag = jsonObjectEdge.get("tag").getAsInt();
                Iterator<edge_data> itr = graph.getE(Integer.parseInt(set.getKey())).iterator();
                edge_data e = itr.next();
                for(int i=0 ; i<counter ; i++){
                    itr.next();
                }
                e.setInfo(info);
                e.setTag(tag);
                counter++;
            }

        }



            return graph;
    }
}
