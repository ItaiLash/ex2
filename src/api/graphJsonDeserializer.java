package api;

import com.google.gson.*;

import java.lang.reflect.Type;

public class graphJsonDeserializer implements JsonDeserializer<directed_weighted_graph> {

    @Override
    public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        directed_weighted_graph graph = new DWGraph_DS();
        JsonArray nodesJsonArray = jsonObject.get("Nodes").getAsJsonArray();
        for (int i = 0; i < nodesJsonArray.size(); i++) {
            JsonObject jsonObjectNode = nodesJsonArray.get(i).getAsJsonObject();
            int key = jsonObjectNode.get("id").getAsInt();
            JsonElement location = jsonObjectNode.get("pos");
            String s = location.getAsString();
            String arr[] = s.split(",");
            double x = Double.parseDouble(arr[0]);
            double y = Double.parseDouble(arr[1]);
            double z = Double.parseDouble(arr[2]);
            geo_location pos = new geoLocation(x, y, z);
            node_data n = new node(key, pos, 0, "White", -1);
            graph.addNode(n);
        }

        JsonArray edgesJsonArray = jsonObject.get("Edges").getAsJsonArray();
        for (int i = 0; i < edgesJsonArray.size(); i++) {
            JsonObject jsonObjectEdge = edgesJsonArray.get(i).getAsJsonObject();
            int src = jsonObjectEdge.get("src").getAsInt();
            int dest = jsonObjectEdge.get("dest").getAsInt();
            double w = jsonObjectEdge.get("w").getAsDouble();
            graph.connect(src,dest,w);
        }
        return graph;
    }
}
