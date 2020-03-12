
struct s {
    int x;
    int y;
    char t;
};

struct s* t;
struct s* r;

void main(){

    t = (struct s*)mcmalloc(sizeof(struct s));
    r = (struct s*)mcmalloc(sizeof(struct s));

    (*r).x = 5;
    (*r).y = 3;
    (*r).t = 'X';
    
    *t = *r;

    print_i((*t).x);
    print_i((*t).y);
    print_c((*t).t);

}