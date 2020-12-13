package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Ex22 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;

    public static void main(String[] args) {
        Thread client = new Thread(new Ex22());
        client.start();
    }

    @Override
    public void run() {
        int game_scenario = 18;
        game_service game = Game_Server_Ex2.getServer(game_scenario);
        directed_weighted_graph gg = init(game);
        game.startGame();
        _win.setTitle("Itai&Liav Pokemons Game");
        int ind = 0;
        long dt = 100;
        while (game.isRunning()) {
            moveAgents(game, gg);
            try {
                if (ind % 1 == 0) {
                    _win.repaint();
                }
                //if(!onPokemonEdge && !onNode) {
                //  Thread.sleep((long)(dt * 1.5));
                //}
                //else{
                Thread.sleep(dt);
                //}

                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }


    private void moveAgents(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        //String str = game.getAgents();
        List<CL_Agent> log = Arena.getAgents(lg, gg);
        _ar.setAgents(log);
        String fs = game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        for (int i = 0; i < log.size(); i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if (dest == -1) {
                dest = nextNode(gg, src, ag);
                //ag.setChoosen();
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
            }
        }
    }

    private static int nextNode(directed_weighted_graph g, int src, CL_Agent agent) {
        int ans;
        api.dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        //agent.choosen = nextPokemonByDis(ga, src);
        List<node_data> lst = nextPokemonByDis(ga, src, agent);
        /*
        if(lst.size() <= 2){
            onPokemonEdge = true;
        }
        else{
            onPokemonEdge = false;
        }
         */
        ans = lst.get(1).getKey();

        return ans;
    }

    private static List<node_data> nextPokemonByDis(dw_graph_algorithms g, int src, CL_Agent agent) {
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        //List<CL_Agent> agents = _ar.getAgents();
        Iterator<CL_Pokemon> itr = poks.listIterator();
        CL_Pokemon closer = null;
        double min = -1;
        List<node_data> lst;
        List<node_data> lst2 = null;
        while (itr.hasNext()) {
            CL_Pokemon current = itr.next();
            if (!current.getIsEaten()) {
                System.out.println("try" + current);
                edge_data e = current.get_edge();
                int type = current.getType();
                double d, v;
                if (e.getDest() > e.getSrc() && type == 1 || e.getDest() < e.getSrc() && type == -1) {
                    d = g.shortestPathDist(src, e.getSrc()) + e.getWeight();
                    v = current.getValue();
                    lst = g.shortestPath(src, e.getSrc());
                    lst.add(g.getGraph().getNode(e.getDest()));
                } else {
                    d = g.shortestPathDist(src, e.getDest()) + e.getWeight();
                    v = current.getValue();
                    lst = g.shortestPath(src, e.getDest());
                    lst.add(g.getGraph().getNode(e.getSrc()));
                }
                if (v / d > min) {
                    min = v / d;
                    closer = current;
                    lst2 = lst;
                }
            }
        }
        closer.setIsEaten();
        //agent.setChoosen(closer);
        System.out.println("agent: " + agent.getID() + " pick " + closer);
        return lst2;
    }

    private static CL_Pokemon nextPokemonByVal(dw_graph_algorithms g, int src, CL_Agent agent) {
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        Iterator<CL_Pokemon> itr = poks.listIterator();
        CL_Pokemon bigger = null;
        double max = 0;
        while (itr.hasNext()) {
            CL_Pokemon current = itr.next();
            System.out.println("try" + current);
            if (!current.getIsEaten()) {
                double d = current.getValue();
                if (d > max) {
                    max = d;
                    bigger = current;
                }
            }
        }
        bigger.setIsEaten();
        System.out.println("pick" + bigger);
        //agent.onHisWay = true;
        return bigger;
    }

    private static CL_Pokemon nextPokemonByValandDis(dw_graph_algorithms g, int src, CL_Agent agent) {
        List<CL_Pokemon> poks = _ar.getPokemons();
        for (CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        Iterator<CL_Pokemon> itr = poks.listIterator();
        CL_Pokemon h = null;
        double max = -1;
        while (itr.hasNext()) {
            CL_Pokemon current = itr.next();
            System.out.println("try" + current);
            if (!current.getIsEaten()) {
                double v = current.getValue();
                double d = g.shortestPathDist(src, current.get_edge().getDest());
                if (v / d > max) {
                    max = v / d;
                    h = current;
                }
            }
        }
        h.setIsEaten();
        System.out.println("pick" + h);
        //agent.onHisWay = true;
        return h;
    }

    public directed_weighted_graph init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        dw_graph_algorithms gra = new DWGraph_Algo();
        File output = new File("graph.txt");
        FileWriter writer;
        try {
            writer = new FileWriter(output);
            writer.write(g);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gra.load("graph.txt");
        directed_weighted_graph gg = gra.getGraph();        //gg.init(g);
        _ar = new Arena();
        _ar.setGraph(gg);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new MyFrame("Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);
        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int numOfAgents = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> poks = Arena.json2Pokemons(game.getPokemons());
            for (int i = 0; i < poks.size(); i++) {
                Arena.updateEdge(poks.get(i), gg);
            }
            for (int i = 0; i < numOfAgents; i++) {
                int n = i % poks.size();
                CL_Pokemon pok = poks.get(n);
                int dest = pok.get_edge().getDest();
                if (pok.getType() < 0) {
                    dest = pok.get_edge().getSrc();
                }
                game.addAgent(dest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gg;
    }

}

