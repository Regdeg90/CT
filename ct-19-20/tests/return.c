struct c {
    int x;
};


int x;
int y[3];
char c;
char ch[4];
struct c t;

int returni(int x){
    if (x==3) {
        print_i(2222);
    }
    return x;
}

char returnc(){

}

struct c returnstruct(){

}


void main(){
    x = 3;
    t.x = 0;
    y[0] = 2;
    y[1] = 4;
    y[2] = 9;
    c = 'd';

    print_i(returni(x));
    print_c('\n');
    print_i(returni(x));
    print_c('\n');
    print_i(y[0]);
    print_c('\n');
    // print_i(returni(t.x));
    // print_c('\n');
    // while (t.x < 3){
    //     print_i(returni(y[t.x]));
    //     print_c('\n');
    //     t.x = t.x +1;
    // }
    
}