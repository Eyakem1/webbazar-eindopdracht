import styles from "../styles/Buttons.module.css";

export default function Button({ kind='default', children, className='', ...rest }){
  const cls = [styles.btn];
  if(kind==='buy') cls.push(styles.blue);
  if(kind==='rent') cls.push(styles.green);
  if(className) cls.push(className);
  return <button className={cls.join(' ')} {...rest}>{children}</button>;
}
