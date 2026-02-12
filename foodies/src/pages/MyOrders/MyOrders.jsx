import { useContext, useEffect, useState } from "react";
import { StoreContext } from "../../context/StoreContext";
import { fetchOrders } from "../../services/orderService";
import { formatPriceBR } from "../../utils/formatPriceBR";
import { assets } from "../../assets/assets";
import "./MyOrders.css";

const MyOrders = () => {
  const { token } = useContext(StoreContext);
  const [data, setData] = useState([]);

  const fetchOrdersHandler = async () => {
    const response = await fetchOrders(token);
    setData(response);
  };

  useEffect(() => {
    if (token) fetchOrdersHandler();
  }, [token]);

  return (
    <div className="container">
      <div className="py-5 row justify-content-center">
        <div className="col-11 card">
          <table className="table table-responsive">
            <tbody>
              {data.map((order, index) => {
                return (
                  <tr key={index}>
                    <td>
                      <img src={assets.logo} alt="" height={48} width={48} />
                    </td>
                    <td>
                      {order.orderedItems.map((item, index) => {
                        if (index === order.orderedItems.length - 1) {
                          return item.name + " x " + item.quantity;
                        } else {
                          return item.name + " x " + item.quantity + ", ";
                        }
                      })}
                    </td>
                    <td>R$ {formatPriceBR(order.amount)}</td>
                    <td>Itens: {order.orderedItems.length}</td>
                    <td className="fw-bold text-capitalize">
                      {order.orderStatus}
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-warning"
                        onClick={fetchOrdersHandler}
                      >
                        <i className="bi bi-arrow-clockwise" />
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default MyOrders;
