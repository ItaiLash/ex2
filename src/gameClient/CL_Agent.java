package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 *This class represent an agent.
 *The class contains various algorithms including getters and setters for all fields,
 *reading from a JSON file and writing to a JSON file.
 * @author itai.Lashover and Liav.Weiss
 */
public class CL_Agent {
    /**
     *Each agent consists of several fields:
     *Unique id, position on the graph, current speed, current edge,current node and accumulated score.
     */
    public static final double EPS = 0.0001;
    private static int _count = 0;
    private static int _seed = 3331;
    private int _id;
    //	private long _key;
    private geo_location _pos;
    private double _speed;
    private edge_data _curr_edge;
    private node_data _curr_node;
    private directed_weighted_graph _gg;
    private CL_Pokemon _curr_fruit;
    private long _sg_dt;
    private double _value;


    /**
     * Default constructor
     * @param g - directed_weighted_graph
     * @param start_node - this agent start node
     */
    public CL_Agent(directed_weighted_graph g, int start_node) {
        _gg = g;
        setMoney(0);
        this._curr_node = _gg.getNode(start_node);
        _pos = _curr_node.getLocation();
        _id = -1;
        setSpeed(0);

    }

    /**
     * Update this agent status(speed, position on the graph, value and more)
     * @param json - represent the game in each move
     */
    public void update(String json) {
        JSONObject line;
        try {
            // "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
            line = new JSONObject(json);
            JSONObject ttt = line.getJSONObject("Agent");
            int id = ttt.getInt("id");
            if (id == this.getID() || this.getID() == -1) {
                if (this.getID() == -1) {
                    _id = id;
                }
                double speed = ttt.getDouble("speed");
                String p = ttt.getString("pos");
                Point3D pp = new Point3D(p);
                int src = ttt.getInt("src");
                int dest = ttt.getInt("dest");
                double value = ttt.getDouble("value");
                this._pos = pp;
                this.setCurrNode(src);
                this.setSpeed(speed);
                this.setNextNode(dest);
                this.setMoney(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return this agent source node.
     * @return nodes' id.
     */
    public int getSrcNode() {
        return this._curr_node.getKey();
    }

    /**
     * Return this agent in JSON format.
     * @return String (as JSON)
     */
    public String toJSON() {
        int d = this.getNextNode();
        String ans = "{\"Agent\":{"
                + "\"id\":" + this._id + ","
                + "\"value\":" + this._value + ","
                + "\"src\":" + this._curr_node.getKey() + ","
                + "\"dest\":" + d + ","
                + "\"speed\":" + this.getSpeed() + ","
                + "\"pos\":\"" + _pos.toString() + "\""
                + "}"
                + "}";
        return ans;
    }

    /**
     * Set this agent value.
     * Used just in the constructor - setMoney to be 0
     * @param v - the new value.
     */
    private void setMoney(double v) {
        _value = v;
    }

    /**
     * Set this agent next node.
     * @param dest - the next nodes' id.
     * @return true if this agent is on edge, otherwise return false.
     */
    public boolean setNextNode(int dest) {
        boolean ans = false;
        int src = this._curr_node.getKey();
        this._curr_edge = _gg.getEdge(src, dest);
        if (_curr_edge != null) {
            ans = true;
        } else {
            _curr_edge = null;
        }
        return ans;
    }

    /**
     * Set this agent node.
     * @param src - this nodes' id
     */
    public void setCurrNode(int src) {
        this._curr_node = _gg.getNode(src);
    }

    /**
     * Return true if this agent on move(on move = on edge), otherwise false/
     * @return true if this agent on move
     */
    public boolean isMoving() {
        return this._curr_edge != null;
    }

    /**
     * toString method.
     * @return agent as String
     */
    public String toString() {
        return toJSON();
    }

    /**
     * Return this agent id.
     * @return id
     */
    public int getID() {
        return this._id;
    }

    /**
     * Return this agent position as a 3DPoint.
     * @return geo_location associated with this agent
     */
    public geo_location getLocation() {
        return _pos;
    }

    /**
     * Return this agent current value.
     * @return the value associated with this agent
     */
    public double getValue() {
        return this._value;
    }

    /**
     * Return this agent next node.
     * If agent not on edge that means he is not moving, return -1
     * @return
     */
    public int getNextNode() {
        int ans;
        if (this._curr_edge == null) {
            ans = -1;
        } else {
            ans = this._curr_edge.getDest();
        }
        return ans;
    }

    /**
     * Return this agent current speed(1,2,5)/
     * @return the speed associated with this agent
     */
    public double getSpeed() {
        return this._speed;
    }

    /**
     * Allows set this agent speed
     * Used just in the constructor - setSpeed to be 0
     * @param v - the new speed
     */
    public void setSpeed(double v) {
        this._speed = v;
    }

    /**
     * Return this agent fruit(Pokemon).
     * @return CK_Pokemon
     */
    public CL_Pokemon get_curr_fruit() {
        return _curr_fruit;
    }

    /**
     * Allows set a CL_Pokemon that this agent want to eat/
     * @param curr_fruit - a Pokemon
     */
    public void set_curr_fruit(CL_Pokemon curr_fruit) {
        this._curr_fruit = curr_fruit;
    }

    /**
     * Return this agent current edge
     * @return edge_data
     */
    public edge_data get_curr_edge() {
        return this._curr_edge;
    }

}
