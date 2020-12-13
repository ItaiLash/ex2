package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

public class Ex2 implements Runnable{
    private static MyFrame _win;
    private static Arena _ar;
    public static void main(String[] a) {
        Thread client = new Thread(new Ex2());
        client.start();

    }

    @Override
    public void run() {
        int scenario_num = 0;
        game_service game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
        //	int id = 999;
        //	game.login(id);
        String g = game.getGraph();
        String pks = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
        init(game);

        game.startGame();
        _win.setTitle("Ex2 - OOP: (NONE trivial Solution) "+game.toString());
        int ind=0;
        long dt=100;

        while(game.isRunning()) {
                moveAgants(game, gg);
            try {
                if(ind%1==0) {_win.repaint();}
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }
    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen (randomly).
     * @param game
     * @param gg
     * @param
     */
    private static void moveAgants(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        System.out.println(lg);
        List<CL_Agent> log = Arena.getAgents(game.getGraph(), gg);
        _ar.setAgents(log);
        //ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        for(int i=0;i<log.size();i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if(dest==-1) {
                dest = nextNode(gg, src);
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
    }
    /**
     * a very simple random walk implementation!
     * @param g
     * @param src
     * @return
     */
    private static int nextNode(directed_weighted_graph g, int src) {
        int ans = -1;
        //Collection<edge_data> ee = g.getE(src);
        CL_Pokemon pok = _ar.getPokemons().listIterator().next();
        Arena.updateEdge(pok,g);
        api.dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        List<node_data> lst = ga.shortestPath(src,pok.get_edge().getDest());
        //Iterator<node_data> itr = lst.listIterator();
        if(lst.size() > 1) {
            ans = lst.get(1).getKey();
        }
        else{
                ans = pok.get_edge().getSrc();
        }
        return ans;
    }
    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);


        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for(int a = 0;a<cl_fs.size();a++) { Arena.updateEdge(cl_fs.get(a),gg);}
            for(int a = 0;a<rs;a++) {
                int ind = a%cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getDest();
                if(c.getType()<0 ) {nn = c.get_edge().getSrc();}

                game.addAgent(nn);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}

