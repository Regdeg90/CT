ant build

pwd
for file in ./tests/*.c;
    do
        filename=$(basename -- "$file")
        echo $filename
        java -cp bin Main -parser tests/$filename dummy.out
    done
