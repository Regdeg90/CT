struct add {
    int p;
    char city[50];
};

struct address
{
//    char* name[50];
//    char street[100];
   char city[50];
   int state[20];
   struct add a;
   int pin;
   int x;
};


struct address add;
struct address sub;


void main(){
    struct address hiya[4];
    struct address hey;
    add.x = 5;
    sub.x = 3;
    print_i(add.x + sub.x); //8
    if (1) {
        struct address hi;
        struct address add;
        add.x = 9;
        print_s((char*)"\nhello\n");
        hi.state[3] = add.x + sub.x;

        hiya[1].state[3] = hi.state[3];
        print_i(hiya[1].state[3]); //12
        print_s((char*)" ");
    }
    hiya[3].a.p = 8;
    hiya[1].x = add.x + sub.x;
    hiya[1].a.city[3] = 'j';
    print_i(hiya[1].x);
    print_s((char*)" ");
    print_i(hiya[3].a.p); //8
    print_c(hiya[1].a.city[3]); //j
    
}