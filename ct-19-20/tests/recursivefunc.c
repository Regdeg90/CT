int n(int x) {
    
    if (x == 0) {
        return 0;
    }
    else {
        x = x-1;
        n(x);
    }
}

void main(){
    int y;
    y =n(3);
    print_i(y);
}