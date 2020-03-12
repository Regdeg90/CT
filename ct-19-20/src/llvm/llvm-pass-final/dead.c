// int foo() {
//   int a = 7;
//   int b = a * 2;
//   int c = b - a;   // dead 
//   int d = c / a;   // dead
//   return b;
// }

int sum (int a , int b)
{
  int i;
  int res = 1;
  for (i =a; i<b; i++)
  {
    res *= i;
  }
  return res;
}

// int foo(int x, int y)
// {
//     int a = 5;
//     a = x + y;
//     if (x > 0)
//     {
//         a = 1;
//     }
//     return a;
// }