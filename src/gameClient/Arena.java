package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph and grabs Pokemons.
 * @author itai.Lashover and Liav.Weiss
 *
 */
public class Arena {

	/**
	 * The Arena contains the graph on which the game will be played on,
	 * a list of agents and Pokemons and updated information on the current state of the game
	 * (location of agents and Pokemon at any given moment,points accumulated and more).
	 */
	public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
	private directed_weighted_graph _gg;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;
	private List<String> _info;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	/**
	 * Default constructor
	 */
	public Arena() {;
		_info = new ArrayList<String>();
	}

	/**
	 * Allows to set this Arena list of Pokemons.
	 * @param f - the list to be loaded.
	 */
	public void setPokemons(List<CL_Pokemon> f) {
		this._pokemons = f;
	}

	/**
	 * Allows to set this Arena list of Agents.
	 * @param f - the list to be loaded.
	 */
	public void setAgents(List<CL_Agent> f) {
		this._agents = f;
	}

	/**
	 * Allows to set this Arena graph.
	 * @param g - the graph to be loaded.
	 */
	public void setGraph(directed_weighted_graph g) {this._gg =g;}//init();}

	/**
	 * Return a list of this Arena Agent.
	 * @return list of Agents
	 */
	public List<CL_Agent> getAgents() {return _agents;}

	/**
	 * Return a list of this Arena Pokemons.
	 * @return list of Pokemons.
	 */
	public List<CL_Pokemon> getPokemons() {return _pokemons;}

	/**
	 * Return this Arena graph.
	 * @return directed_weighted_graph
	 */
	public directed_weighted_graph getGraph() {
		return _gg;
	}

	/**
	 * Return this Arena current info.
	 * @return info
	 */
	public List<String> get_info() {
		return _info;
	}

	/**
	 * Allows change this Arena info.
	 * @param _info List of Strings to be loaded.
	 */
	public void set_info(List<String> _info) {
		this._info = _info;
	}

	/**
	 * Return List of agents.
	 * The method gets a graph and a JSON format string and read all the agent from the string.
	 * @param aa - JSON format string
	 * @param gg - directed_weighted_graph
	 * @return List of Agents
	 */
	public static List<CL_Agent> getAgents(String aa, directed_weighted_graph gg) {
		ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
		try {
			JSONObject ttt = new JSONObject(aa);
			JSONArray ags = ttt.getJSONArray("Agents");
			for(int i=0;i<ags.length();i++) {
				CL_Agent c = new CL_Agent(gg,0);
				c.update(ags.get(i).toString());
				ans.add(c);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * Return List of Pokemon.
	 * The method gets a JSON format string and read all the Pokemons from the string.
	 * @param fs - a JSON format string
	 * @return List of Pokemons
	 */
	public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
		ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
		try {
			JSONObject ttt = new JSONObject(fs);
			JSONArray ags = ttt.getJSONArray("Pokemons");
			for(int i=0;i<ags.length();i++) {
				JSONObject pp = ags.getJSONObject(i);
				JSONObject pk = pp.getJSONObject("Pokemon");
				int t = pk.getInt("type");
				double v = pk.getDouble("value");
				//double s = 0;//pk.getDouble("speed");
				String p = pk.getString("pos");
				CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
				ans.add(f);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return ans;
	}

	/**
	 * Update a specific Pokemons' edge.
	 * @param fr - CL_Pokemon
	 * @param g - directed_weighted_graph
	 */
	public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
		//	oop_edge_data ans = null;
		Iterator<node_data> itr = g.getV().iterator();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
				if(f) {fr.set_edge(e);}
			}
		}
	}


	private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {

		boolean ans = false;
		double dist = src.distance(dest);
		double d1 = src.distance(p) + p.distance(dest);
		if(dist>d1-EPS2) {ans = true;}
		return ans;
	}
	private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
		geo_location src = g.getNode(s).getLocation();
		geo_location dest = g.getNode(d).getLocation();
		return isOnEdge(p,src,dest);
	}
	private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
		int src = g.getNode(e.getSrc()).getKey();
		int dest = g.getNode(e.getDest()).getKey();
		if(type<0 && dest>src) {return false;}
		if(type>0 && src>dest) {return false;}
		return isOnEdge(p,src, dest, g);
	}

	private static Range2D GraphRange(directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		double x0=0,x1=0,y0=0,y1=0;
		boolean first = true;
		while(itr.hasNext()) {
			geo_location p = itr.next().getLocation();
			if(first) {
				x0=p.x(); x1=x0;
				y0=p.y(); y1=y0;
				first = false;
			}
			else {
				if(p.x()<x0) {x0=p.x();}
				if(p.x()>x1) {x1=p.x();}
				if(p.y()<y0) {y0=p.y();}
				if(p.y()>y1) {y1=p.y();}
			}
		}
		Range xr = new Range(x0,x1);
		Range yr = new Range(y0,y1);
		return new Range2D(xr,yr);
	}

	public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
		Range2D world = GraphRange(g);
		Range2Range ans = new Range2Range(world, frame);
		return ans;
	}

	/**
	 * Return a Pokemon by his position(Point3D) on a graph
	 * @param p - Point3D
	 * @return CL_Pokemon
	 */
	public CL_Pokemon getPokByPoint(Point3D p){
		for(CL_Pokemon pok : _pokemons){
			if(pok.getLocation().equals(p)){
				return pok;
			}
		}
		return null;
	}

}
