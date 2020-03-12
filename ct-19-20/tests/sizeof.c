struct s {
    int x;
    int y[3];
    char c;
    char r[3];
};

struct t {
    int x[4];
    struct s p;
};


void main(){
    print_i(sizeof(struct t));
    print_c('\n');
    print_i(sizeof(struct t*));
    print_c('\n');
    print_i(sizeof(int));
    print_c('\n');
    print_i(sizeof(int*));
    print_c('\n');
    print_i(sizeof(char));
    print_c('\n');
    print_i(sizeof(char*));


}