ant build

pwd
for file in ./tests/*.c;
    do
        filename=$(basename -- "$file")
        echo $filename
        java -cp bin Main -lexer tests/$filename dummy.out > output
        diff -s output tests/"${filename//.c}correct.txt"
    done
