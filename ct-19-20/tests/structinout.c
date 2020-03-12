struct s {
    int x;

    int w[4];
    char c;
};



struct s f;
struct s t;

struct s retstruct(int x, struct s g){

    // print_i(x);
    // print_c(g.c);
    // print_i(g.x);
    // print_i(g.x == 2);
    // print_i(g.c == 'C');
    // print_i(g.x == 2 || f.x == 2);

    if (g.x == 2 && g.c == 'C') {
        g.x = 4;
        g.c = 'T';
    }
    

    return g;
}

int funcall() {
    print_s((char*)"\nDoing function call\n");
    return 1;
}

void main(){

    f.x = 2;
    f.c = 'C';
    t.x = f.x;
    // print_i(t.x == 2);
    // print_i(f.x == 2 || funcall());
    // f = t;
    t = retstruct(1,f);


    print_i(t.x);
    print_c(t.c);
}