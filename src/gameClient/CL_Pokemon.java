package gameClient;
import api.edge_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * This Class represent a Pokemon.
 * The class contains various algorithms including getters and setters for all fields
 * and reading a Pokemon from a JSON file.
 * Each Pokemon contains several fields, including:
 * The edge on which the Pokemon is located, its position on the graph,
 * the amount of score that will be collected on eating this Pokemon and the type of the Pokemon (positive value -> Pokemon is on an ascending edge (aka 4-->8), negative value -> The Pokemon is on a descending edge (aka 7 --> 2)).
 * @author itai.Lashover and Liav.Weiss
 */
public class CL_Pokemon {
	/**
	 * Each Pokemon contains several fields, including:
	 * The edge on which the Pokemon is located, its position on the graph,
	 * the amount of score that will be collected on eating this Pokemon
	 * and the type of the Pokemon (positive value -> Pokemon is on an ascending edge (aka 4-->8),
	 * negative value -> The Pokemon is on a descending edge (aka 7 --> 2)).
	 */
	private edge_data _edge;
	private double _value;
	private int _type;
	private Point3D _pos;
	private double min_dist;
	private int min_ro;

	/**
	 * Default constructor
	 * @param p
	 * @param t
	 * @param v
	 * @param s
	 * @param e
	 */
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		_type = t;
	//	_speed = s;
		_value = v;
		set_edge(e);
		_pos = p;
		min_dist = -1;
		min_ro = -1;
	}

	/**
	 * Read JSON string and return him as a CL_Pokemon.
	 * @param json - String represent JSON file
	 * @return CL_Pokemon
	 */
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * toString method
	 * @return this CL_Pokemon as String`
	 */
	public String toString() {return "F:{v="+_value+", t="+_type+"}";}

	/**
	 * This method returns the edge that the Pokemon is on
	 * @return the edge of this pokemon located
	 */
	public edge_data get_edge() {
		return _edge;
	}

	/**
	 * Allows changing the edge that the pokemon is on.
	 * @param _edge the new edge.
	 */
	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	/**
	 * Returns the location of this pokemon, if none return null.
	 * @return the location of this pokemon.
	 */
	public Point3D getLocation() {
		return _pos;
	}

	/**
	 * Returns the type of this pokemon(positive value -> Pokemon is on an ascending edge,
	 * negative value -> The Pokemon is on a descending edge)
	 * @return the type of this pokemon.
	 */
	public int getType() {return _type;}

	/**
	 * Returns the value of this pokemon(How much is it worth).
	 * @return the value of this pokemon.
	 */
	public double getValue() {return _value;}




}
