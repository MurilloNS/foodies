import axios from "axios";

const API_URL = "http://localhost:8080/api/orders";

export const createOrder = async (orderData, token) => {
  try {
    const response = await axios.post(API_URL + "/create", orderData, {
      headers: { Authorization: `Bearer ${token}` },
    });

    return response.data;
  } catch (error) {
    console.error("ERROR creating order", error);
    throw error;
  }
};

export const deleteOrderById = async (orderId, token) => {
  try {
    await axios.delete(API_URL + "/" + orderId, {
      headers: { Authorization: `Bearer ${token}` },
    });
  } catch (error) {
    console.error("ERROR deleting order", error);
    throw error;
  }
};

export const fetchOrders = async (token) => {
  try {
    const response = await axios.get(API_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });

    return response.data;
  } catch (error) {
    console.error("ERROR fetching orders", error);
  }
};
