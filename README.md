Manually created a database which performs high level commands such as Create, Insert, select. And supports all major datatypes.
Used an indexing technique where while performing the create and insert operations We create the correcponding table and column files and index all the columns and rows for the corresponding indexs.
Hence while performing the operations like where, joins we retrive the information of indexes from the corresponding column files and retrive the index from the primary table file.
