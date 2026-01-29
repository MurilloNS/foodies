import axios from "axios";

const API_URL = "http://localhost:8080/api";

export const registerUser = async (data) => {
  try {
    const response = await axios.post(API_URL + "/register", data);
    return response;
  } catch (e) {
    console.error("ERROR", e);
    throw e;
  }
};

export const login = async (data) => {
  try {
    const response = await axios.post(API_URL + "/login", data);
    return response;
  } catch (e) {
    console.error("ERROR", e);
    throw e;
  }
};
