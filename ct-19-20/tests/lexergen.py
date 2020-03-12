import random
import string

def lexer():
    tokens = [" ident ", "=", "{", "}", "(", ")", "[","]",";",",","int", "void", "char", "if", "else", "while", "return", "struct", "sizeof", "#include", "\"stri\\ng\"", "123", "\'\\r\'", "&&", "||", "==", "!=", "<", ">", "<=", ">=", "+", "-", "+*", "/%", "%", "."]
    tokenname = ["IDENTIFIER(ident)", "ASSIGN", "LBRA", "RBRA", "LPAR", "RPAR", "LSBR", "RSBR", "SC", "COMMA", "INT", "VOID", "CHAR", "IF", "ELSE", "WHILE", "RETURN", "STRUCT", "SIZEOF", "INCLUDE", "STRING_LITERAL(stri\\ng)", "INT_LITERAL(123)", "CHAR_LITERAL(\\r)", "AND", "OR", "EQ", "NE", "LT", "GT", "LE", "GE", "PLUS", "MINUS", "PLUS\nASTERIX", "DIV\nREM", "REM", "DOT"]

    for i in range(500000):
        x = random.randint(0,len(tokens)-1)
        f.write("\n\n\n\n \t \r /*multine comment*/ \n \n //single line comment \n \n")
        f.write(" ")
        f.write(tokens[x])
        f.write(" ")
        f.write("\n\n\n\n \t \r /*multine comment*/ \n \n //single line comment \n \n")
        g.write(tokenname[x] + '\n')
    

tokens = [" ident ", "=", "{", "}", "(", ")", "[","]",";",",","int", "void", "char", "if", "else", "while", "return", "struct", "sizeof", "#include", "\"string\"", "123", "\'c\'", "&&", "||", "==", "!=", "<", ">", "<=", ">=", "+", "-", "+*", "/%", "%", "."]
tokenname = ["IDENTIFIER(ident)", "ASSIGN", "LBRA", "RBRA", "LPAR", "RPAR", "LSBR", "RSBR", "SC", "COMMA", "INT", "VOID", "CHAR", "IF", "ELSE", "WHILE", "RETURN", "STRUCT", "SIZEOF", "INCLUDE", "STRING_LITERAL(string)", "INT_LITERAL(123)", "CHAR_LITERAL(c)", "AND", "OR", "EQ", "NE", "LT", "GT", "LE", "GE", "PLUS", "MINUS", "PLUS\nASTERIX", "DIV\nREM", "REM", "DOT"]

for i in range(100):
    f = open("testlexer" + str(i) + ".c","w+")
    g = open("testlexer" + str(i)+ "correct" + ".txt","w+")
    lexer()
    g.write("Lexing: pass\n")
    f.close()
    g.close()
for i in range(0,len(tokens)):
    print(tokens[i])
    print(tokenname[i])

for i in range(0,len(tokens)):
    f = open("testlexerpair" + str(i) + ".c","w+")
    g = open("testlexerpair" + str(i)+ "correct" + ".txt","w+")
    for j in range(0,len(tokens)):
        f.write("\n\n\n\n \t \r /*multine comment*/ \n \n //single line comment \n \n")
        f.write(tokens[i])
        f.write("\n\n\n\n \t \r /*multine comment*/ \n \n //single line comment \n \n")
        f.write(tokens[j])
        f.write("\n\n\n\n \t \r /*multine comment*/ \n \n //single line comment \n \n")
        g.write(tokenname[i]+ '\n')
        g.write(tokenname[j] + '\n')
    g.write("Lexing: pass\n")
