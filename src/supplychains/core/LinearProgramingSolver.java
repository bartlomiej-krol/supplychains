package supplychains.core;

import net.sf.javailp.*;

import java.util.List;

public class LinearProgramingSolver {


    public static Result resolve(List<Trip> trips, List<Equation> eq){
        SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds


        Problem problem = new Problem();

        Linear linear = new Linear();
        for (Trip t :
                trips) {
            linear.add(t.cost, t.name);
        }

        problem.setObjective(linear, OptType.MIN);

        for (Equation e :
                eq) {
            Linear lin = new Linear();
            for (Trip t :
                    e.add) {
                lin.add(1, t.name);
            }
            for (Trip t :
                    e.sub) {
                lin.add(-1, t.name);
            }
            problem.add(lin, e.sign.txt, Math.abs(e.val));
        }

        Solver solver = factory.get();
        Result result = solver.solve(problem);

        System.out.println("Wynik: " + result.getObjective());
        for (Trip t :
                trips) {
            System.out.println("Droga: " + t.name
                    + " " +Math.round((Double) result.getPrimalValue(t.name)));
        }
        return result;

    }

}
