from pymongo import MongoClient

# client = MongoClient("mongodb://root:password@mongodb:27017/")
client = MongoClient("mongodb://root:password@localhost:27017/")

db = client["product_db"]

