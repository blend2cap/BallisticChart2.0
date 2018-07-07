package sample;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

class GavreParams {
    double maxFps, A, B, C, D;
    public static ImmutableList<GavreParams> G7;

    ImmutableList.Builder<GavreParams> builder = ImmutableList.builder();
    {
        G7 = builder.add(new GavreParams(669.256492536075, 1.28556228919856E-09, 1.17070501061523E-06, -2.02335478295562E-03, 1.32635024122294E+02)).
                add(new GavreParams(925.753343283004, 6.54807168890507E-07, -1.39316763820100E-03, 9.93372635483802E-01, -1.04911616991713E+02)).
                add(new GavreParams(958.947676806284, 5.13898302057077E-06, -1.30942169110207E-02, 1.11657572249166E+01, -3.05169943365642E+03)).
                add(new GavreParams(1083.155619565560, 3.98860555607845E-05, -1.09443796188559E-01, 9.98208731424153E+01, -3.01071097408286E+04)).
                add(new GavreParams(1173.581477667430, 3.87434680039917E-04, -1.34266806214912E+00, 1.55104613636709E+03, -5.96819096492053E+05)).
                add(new GavreParams(1214.033823371580, 2.20645770113675E-05, -8.02263808478340E-02, 9.71503915929359E+01, -3.87348255793796E+04)).
                add(new GavreParams(1975.516274264780, 1.03797366315346E-07, -4.64880793914417E-04, 5.46998101560906E-01, 2.81486691385228E+02)).
                add(new GavreParams(2731.324956487630, -3.22195682940000E-08, 2.44058569354761E-04, -6.69779088763454E-01, 9.67157591099752E+02)).
                add(new GavreParams(4427.742014494870, 3.79385526397504E-09, -3.62541120803069E-05, 6.01578005766827E-02, 3.30821955831662E+02)).
                add(new GavreParams(0,         6.04390204413459E+02, -4.17942000000000E-04, 1.20772860224648E+02, 0.00000000000000E+00)).build();
    }

    private GavreParams(double maxFps, double a, double b, double c, double d) {
        this.maxFps = maxFps;
        A = a;
        B = b;
        C = c;
        D = d;
    }


}
