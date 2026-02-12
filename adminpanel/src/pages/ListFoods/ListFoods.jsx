import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "./ListFoods.css";
import { deleteFood, getAllFoods } from "../../services/foodService";

const ListFoods = () => {
  const [list, setList] = useState([]);

  const fetchList = async () => {
    try {
      const data = await getAllFoods();
      setList(data);
    } catch (e) {
      toast.error("Error while reading foods.");
    }
  };

  const removeFood = async (foodId) => {
    try {
      await deleteFood(foodId);
      toast.success("Food removed.");
      await fetchList();
    } catch (error) {
      toast.error("Error occurred while removing the food.");
    }
  };

  useEffect(() => {
    fetchList();
  }, []);

  return (
    <div className="py-5 row justify-content-center">
      <div className="col-11 card">
        <table className="table">
          <thead>
            <tr>
              <th>Image</th>
              <th>Name</th>
              <th>Category</th>
              <th>Price</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {list.map((item, index) => {
              return (
                <tr key={index}>
                  <td>
                    <img
                      src={item.imageUrl}
                      alt="Food image"
                      height={48}
                      width={48}
                    />
                  </td>
                  <td>{item.name}</td>
                  <td>{item.category}</td>
                  <td>R$ {item.price}</td>
                  <td className="text-danger">
                    <i
                      className="bi bi-x-circle-fill"
                      onClick={() => removeFood(item.id)}
                    />
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ListFoods;
