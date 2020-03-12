
#include "minic-stdlib.h"



struct s {
    int x;
    int y;
};

int* cp;
void main() {
    
    int* x;

    struct s* b; 
    struct s* t;
    // struct s r;
    cp = (int*)mcmalloc(sizeof(int));
    *cp = 6;
    x = cp;

    b = (struct s*)mcmalloc(sizeof(struct s));
    t = (struct s*) mcmalloc(sizeof(struct s));

    (*b).x = 5;
    (*b).y = 2;

    (*t).x = 9;
    (*t).y = 4;


    (*b).x = *cp;

    if(*x == 6) {
        *x = 4;
        (*b).y = 7;
    }

    // r = *b;
    print_i((*b).x);
    print_i((*b).y);
    print_i((*t).x);
    print_i((*t).y);
    print_i(*cp);
}