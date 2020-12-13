package gameClient;

import Server.Game_Server_Ex2;
import api.directed_weighted_graph;
import api.game_service;
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

public class ThreadTest {


    public static void main(String[] args) {
        Thread1 t1 = new Thread1();
        Thread2 t2 = new Thread2();
        Thread client1 = new Thread(t1);
        Thread client2 = new Thread(t2);
        client1.start();
        client2.start();
    }
}

class MyGame {

}

class Thread1 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;
    private static boolean onPokemonEdge = false;
    private static boolean onNode = true;
    private static String currentString = null;
    private static int count = 0;
    @Override
    public void run() {
        int game_scenario = 0;
        game_service game = Game_Server_Ex2.getServer(game_scenario);
        directed_weighted_graph gg = init(game);
        game.startGame();
        _win.setTitle("Itai&Liav Pokemons Game");
        int ind = 0;
        long dt = 100;
        //currentString = game.move();
        //moveAgants(game, gg,currentString);
        while (game.isRunning()) {
            moveAgants(game, gg);
            // moveAgants(game, gg,currentString);
            try {
                if (ind % 1 == 0) {
                    _win.repaint();
                }
                if (!onPokemonEdge && !onNode) {
                    Thread.sleep((long) (dt * 1.5));
                } else {
                    Thread.sleep(dt);
                }

                ind++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }


    private void moveAgants(game_service game, directed_weighted_graph gg){
        String lg = game.move();
        System.out.println(lg);
        String str = game.getAgents();
        List<CL_Agent> log = Arena.getAgents(lg, gg);
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
            onNode = false;
            if(dest==-1) {
                onNode = true;
                dest = nextNode(gg, src);
                //this.notify();
                game.chooseNextEdge(ag.getID(), dest);
                //this.notify();
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }

        }
    }

    private static int nextNode(directed_weighted_graph g, int src) {
        int ans;
        //Collection<edge_data> ee = g.getE(src);
        //CL_Pokemon pok = _ar.getPokemons().listIterator().next();
        //Arena.updateEdge(pok, g);
        api.dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        CL_Pokemon pok = nextPokemonByDis(ga,src);
        List<node_data> lst = ga.shortestPath(src, pok.get_edge().getDest());
        //Iterator<node_data> itr = lst.listIterator();
        if(lst.size() <= 2){
            onPokemonEdge = true;
        }
        else{
            onPokemonEdge = false;
        }
        if (lst.size() > 1) {
            ans = lst.get(1).getKey();
        } else {

            ans = pok.get_edge().getSrc();
        }
        return ans;
    }

    private static CL_Pokemon nextPokemonByDis(dw_graph_algorithms g, int src) {
        List<CL_Pokemon> poks = _ar.getPokemons();
        for(CL_Pokemon pok : poks) {
            Arena.updateEdge(pok, g.getGraph());
        }
        Iterator<CL_Pokemon> itr = poks.listIterator();
        double max = Double.MAX_VALUE;
        CL_Pokemon closer = itr.next();
        while(itr.hasNext()){
            CL_Pokemon current = itr.next();
            double d = g.shortestPathDist(src, current.get_edge().getDest());
            if(d<max) {
                max = d;
                closer = current;
            }
        }
        return closer;
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

class Thread2 implements Runnable {
    @Override
    public void run() {

    }
}