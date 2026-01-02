import axios from "axios";

const API_URL = "http://localhost:8080/api/foods";

export const fetchFoodList = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (e) {
    console.error("ERROR", e);
    throw e;
  }
};
