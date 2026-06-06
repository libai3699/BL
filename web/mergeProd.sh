# 将dev代码合并到prod的快捷命令
# 要保证当前分支是dev再运行这个
git checkout prod && git pull origin && git merge dev && git push origin && git checkout dev