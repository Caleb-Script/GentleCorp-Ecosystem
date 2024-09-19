from sqlalchemy import Column, Integer, MetaData, String, Table, create_engine, ARRAY

from databases import Database

from ..core import settings


engine = create_engine(settings.DATABASE_URL)
metadata = MetaData()
