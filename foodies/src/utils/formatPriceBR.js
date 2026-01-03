export const formatPriceBR = (value) => {
  if (value === null || value === undefined) return "0,00";

  const number = Number(value);

  if (Number.isNaN(number)) return "0,00";

  return value.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};
