import os
import pandas as pd
from sqlalchemy import create_engine, Column, String, Float
from sqlalchemy.orm import sessionmaker, declarative_base

# Setup paths to dynamically find the root folder and the CSV
current_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(current_dir)
db_path = os.path.join(root_dir, "app.db")
csv_path = os.path.join(root_dir, "gig_workers_db.csv")

# SQLite Engine & Session Setup
DATABASE_URL = f"sqlite:///{db_path}"
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# 3. Define the ORM Model
class GigWorker(Base):
    __tablename__ = "gig_workers"

    rider_id = Column(String, primary_key=True, index=True)
    join_date = Column(String)
    last_4_weeks_hours = Column(Float)
    last_4_weeks_earnings = Column(Float)
    account_status = Column(String)

# Database Seeding Function
def init_db_and_seed():
    """Creates the tables and seeds them from the CSV if empty."""
    Base.metadata.create_all(bind=engine)
    
    db = SessionLocal()
    # Check if database is already populated
    if db.query(GigWorker).first() is None:
        print("Database is empty. Seeding from CSV...")
        if os.path.exists(csv_path):
            df = pd.read_csv(csv_path)
            for _, row in df.iterrows():
                worker = GigWorker(
                    rider_id=row["rider_id"],
                    join_date=row["join_date"],
                    last_4_weeks_hours=row["last_4_weeks_hours"],
                    last_4_weeks_earnings=row["last_4_weeks_earnings"],
                    account_status=row["account_status"]
                )
                db.add(worker)
            db.commit()
            print(f"Successfully loaded {len(df)} workers into SQLite database!")
        else:
            print("Error: gig_workers_db.csv not found in root directory.")
    else:
        print("Database already seeded. Ready to go.")
    db.close()

if __name__ == "__main__":
    init_db_and_seed()
