lines_seen = set() # holds lines already seen
outfile = open("stopwords", "w")
for line in open("stopword", "r"):
    if line not in lines_seen: # not a duplicate
        lines_seen.add(line)
lines_seen = list(lines_seen)
lines_seen.sort()
for line in lines_seen:
	outfile.write(line)
outfile.close()