import axios from "axios";

const API_URL = "http://localhost:8080/api/orders";

export const fetchAllOrders = async () => {
  try {
    const response = await axios.get(API_URL + "/all");
    return response.data;
  } catch (error) {
    console.error("ERROR fetching orders", error);
  }
};

export const updateStatus = async (event, orderId) => {
  try {
    const response = await axios.patch(
      API_URL + "/status/" + orderId + `?status=${event.target.value}`,
    );

    return response;
  } catch (error) {
    console.error("ERROR fetching orders", error);
  }
};
