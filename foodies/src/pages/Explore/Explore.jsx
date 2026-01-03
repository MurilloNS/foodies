import { useState } from "react";
import FoodDisplay from "../../components/FoodDisplay/FoodDisplay";

const Explore = () => {
  const [category, setCategory] = useState("All");
  const [searchText, setSearchText] = useState("");

  return (
    <>
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6">
            <form onSubmit={(e) => e.preventDefault()}>
              <div className="input-group mb-3">
                <select
                  className="form-select mt-2"
                  style={{ maxWidth: "150px" }}
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <option value="All">All</option>
                  <option value="Ice Cream">Ice Cream</option>
                  <option value="Salad">Salad</option>
                  <option value="Burger">Burger</option>
                  <option value="Pizza">Pizza</option>
                  <option value="Drink">Drink</option>
                  <option value="Dessert">Dessert</option>
                  <option value="Snack">Snack</option>
                </select>
                <input
                  className="form-control mt-2"
                  type="text"
                  placeholder="Search your favorite dish..."
                  onChange={(e) => setSearchText(e.target.value)}
                  value={searchText}
                />
                <button className="btn btn-primary mt-2" type="submit">
                  <i className="bi bi-search" />
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <FoodDisplay category={category} searchText={searchText} />
    </>
  );
};

export default Explore;
