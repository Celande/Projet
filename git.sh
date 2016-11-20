# !bash/bin

rm ./build/*.class
git add *
git commit
git pull
git push -u json_remote new_json
# git push -u origin master
