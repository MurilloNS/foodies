import { useEffect, useState } from "react";
import { fetchAllOrders, updateStatus } from "../../services/orderService";
import { formatPriceBR } from "../../utils/formatPriceBR";
import { assets } from "../../assets/assets";

const Orders = () => {
  const [data, setData] = useState([]);

  const fetchAllOrdersHandler = async () => {
    const response = await fetchAllOrders();
    setData(response);
  };

  const updateStatusHandler = async (event, orderId) => {
    const response = await updateStatus(event, orderId);

    if (response.status === 200) {
      fetchAllOrdersHandler();
    }
  };

  useEffect(() => {
    fetchAllOrdersHandler();
  }, []);

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
                      <div>
                        {order.orderedItems.map((item, index) => {
                          if (index === order.orderedItems.length - 1) {
                            return item.name + " x " + item.quantity;
                          } else {
                            return item.name + " x " + item.quantity + ", ";
                          }
                        })}
                      </div>
                      <div>{order.userAddress}</div>
                    </td>
                    <td>R$ {formatPriceBR(order.amount)}</td>
                    <td>Itens: {order.orderedItems.length}</td>
                    <td>
                      <select
                        className="form-control"
                        onChange={(event) =>
                          updateStatusHandler(event, order.id)
                        }
                        value={order.orderStatus}
                      >
                        <option value="Food Preparing">Food Preparing</option>
                        <option value="Out for delivery">
                          Out for delivery
                        </option>
                        <option value="Delivered">Delivered</option>
                      </select>
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

export default Orders;
