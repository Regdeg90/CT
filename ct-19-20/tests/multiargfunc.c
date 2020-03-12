int foo(int x, int p) {
    print_s((char*)"\nhere \n");
    print_i(x);
}

int fun(int x, int a, char b, char c,char d) {
    print_i(x);
    print_i(a);
    print_c(b);
    print_c(c);
    print_c(d);
    foo(a, x);
    return x;

}

void main() {
    // int x;
    int a;
    // char b;
    // char c;
    // char d;
    
     a = fun(2,3,'d','e','w');

     print_c('\n');
     print_i(a);
}